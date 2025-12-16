import path from 'path';
import fs from 'fs';
import {LoadContext, Plugin} from '@docusaurus/types';
import matter from 'gray-matter'; // <--- Import gray-matter here

export interface ExtensionMetadata {
    id: string;
    title: string;
    description: string;
    author: string;
    image?: string;
    downloadUrl: string;
    sourceUrl?: string;
    tags: string[];
    permalink: string;
    content: string;
}

export default function extensionsPlugin(context: LoadContext): Plugin<ExtensionMetadata[]> {
    return {
        name: 'betterteams-extensions-loader',

        getPathsToWatch() {
            return [path.resolve(context.siteDir, 'extensions', '**/*.{md,mdx}')];
        },

        async loadContent() {
            const extensionsDir = path.resolve(context.siteDir, 'extensions');
            if (!fs.existsSync(extensionsDir)) {
                return [];
            }

            const files = fs.readdirSync(extensionsDir).filter(f => f.endsWith('.md') || f.endsWith('.mdx'));

            return files.map((file) => {
                const filePath = path.join(extensionsDir, file);
                const fileContent = fs.readFileSync(filePath, 'utf-8');

                // Use gray-matter to parse the file
                const {data: frontMatter, content} = matter(fileContent);

                const id = path.basename(file, path.extname(file));

                return {
                    id,
                    title: frontMatter.title as string,
                    description: frontMatter.description as string,
                    author: frontMatter.author as string,
                    image: frontMatter.image as string,
                    downloadUrl: frontMatter.downloadUrl as string,
                    sourceUrl: frontMatter.sourceUrl as string,
                    tags: (frontMatter.tags || []) as string[],
                    permalink: `/extensions/${id}`,
                    content: content,
                };
            });
        },

        async contentLoaded({ content, actions }) {
            const { createData, addRoute } = actions;

            // Create the Main List Page
            const extensionsJsonPath = await createData(
                'extensionsList.json',
                JSON.stringify(content)
            );

            // Add route for /extensions
            addRoute({
                path: '/extensions',
                component: '@site/src/components/Extensions/ExtensionList.tsx',
                modules: {
                    extensions: extensionsJsonPath,
                },
                exact: true,
            });

            // Create Individual Extension Pages
            await Promise.all(
                content.map(async (extension) => {
                    const extensionJsonPath = await createData(
                        `${extension.id}.json`,
                        JSON.stringify(extension)
                    );

                    addRoute({
                        path: extension.permalink,
                        component: '@site/src/components/Extensions/ExtensionDetail.tsx',
                        modules: {
                            metadata: extensionJsonPath,
                        },
                        exact: true,
                    });
                })
            );
        },
    };
}