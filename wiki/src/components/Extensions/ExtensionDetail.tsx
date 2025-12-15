import React, {JSX} from 'react';
import Layout from '@theme/Layout';
import Markdown from 'react-markdown';
import Link from '@docusaurus/Link';
import remarkGfm from 'remark-gfm';
import rehypeRaw from 'rehype-raw';

interface ExtensionData {
    title: string;
    description: string;
    content: string;
    downloadUrl: string;
    sourceUrl?: string;
    image?: string;
    author: string;
    tags: string[];
}

function preprocessMarkdown(markdown: string): string {
    const admonitionRegex = /:::(note|tip|info|warning|danger)\s*([\s\S]*?)\n:::/g;

    return markdown.replace(admonitionRegex, (match, type, content) => {
        const typeMap: Record<string, string> = {
            note: 'secondary',
            tip: 'success',
            info: 'info',
            warning: 'warning',
            danger: 'danger',
        };
        const alertClass = typeMap[type] || 'secondary';
        const title = type.charAt(0).toUpperCase() + type.slice(1);

        return `
<div class="alert alert--${alertClass}">
  <div <span>ℹ️</span> ${title} </div>
  <div class="admonitionContent">${content}</div>
</div>`;
    });
}

export default function ExtensionDetail({ metadata }: { metadata: ExtensionData }): JSX.Element {
    const processedContent = preprocessMarkdown(metadata.content);

    return (
        <Layout title={metadata.title} description={metadata.description}>
            <main className="container padding-vert--lg">
                <div className="row">
                    <div className="col col--3">
                        <div className="card padding--md">
                            {metadata.image && (
                                <div className="text--center margin-bottom--md">
                                    <img src={metadata.image} alt={metadata.title} style={{maxWidth: '100%', borderRadius: '8px'}} />
                                </div>
                            )}
                            <div className="margin-bottom--md">
                                <strong>Author:</strong> {metadata.author}
                            </div>
                            <div className="margin-bottom--md">
                                {metadata.tags.map(tag => (
                                    <span key={tag} className="badge badge--secondary margin-right--xs">{tag}</span>
                                ))}
                            </div>
                            <Link to={metadata.downloadUrl} className="button button--primary button--block margin-bottom--sm">
                                Download
                            </Link>
                            {metadata.sourceUrl && (
                                <Link to={metadata.sourceUrl} className="button button--secondary button--block">
                                    Source Code
                                </Link>
                            )}
                            <div className="margin-top--md text--center">
                                <Link to="/extensions">← Back to Extensions</Link>
                            </div>
                        </div>
                    </div>

                    <div className="col col--9">
                        <div className="card padding--lg">
                            <Markdown
                                remarkPlugins={[remarkGfm]}
                                rehypePlugins={[rehypeRaw]}
                            >
                                {processedContent}
                            </Markdown>
                        </div>
                    </div>
                </div>
            </main>
        </Layout>
    );
}