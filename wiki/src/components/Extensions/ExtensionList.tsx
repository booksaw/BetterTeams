import React, {JSX, useMemo, useState} from 'react';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';
import Heading from '@theme/Heading';
import clsx from 'clsx';
import styles from './styles.module.css';

interface Extension {
    id: string;
    title: string;
    description: string;
    author: string;
    image?: string;
    tags: string[];
    permalink: string;
}

export default function ExtensionList({ extensions }: { extensions: Extension[] }): JSX.Element {
    const [searchTerm, setSearchTerm] = useState('');

    const filteredExtensions = useMemo(() => {
        return extensions.filter((ext) => {
            const searchLower = searchTerm.toLowerCase();
            return (
                ext.title.toLowerCase().includes(searchLower) ||
                ext.description.toLowerCase().includes(searchLower) ||
                ext.tags.some(tag => tag.toLowerCase().includes(searchLower))
            );
        });
    }, [extensions, searchTerm]);

    return (
        <Layout title="Extensions" description="Browse BetterTeams Extensions">
            <header className={clsx('hero hero--primary', styles.heroBanner)}>
                <div className="container">
                    <Heading as="h1" className="hero__title">BetterTeams Extensions</Heading>
                    <div className={styles.searchContainer}>
                        <input
                            type="text"
                            placeholder="Search extensions..."
                            className={styles.searchInput}
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>
            </header>

            <main className="container padding-vert--lg">
                {filteredExtensions.length > 0 ? (
                    <div className={styles.grid}>
                        {filteredExtensions.map((ext) => (
                            <div key={ext.id} className={clsx('card', styles.card)}>
                                <div className={styles.cardHeader}>
                                    {ext.image && (
                                        <div className={styles.cardImageContainer}>
                                            <img src={ext.image} alt={ext.title} className={styles.cardIcon} />
                                        </div>
                                    )}
                                    <div className={styles.cardTitleContainer}>
                                        <Heading as="h3" className={styles.cardTitle}>{ext.title}</Heading>
                                        <span className={styles.cardAuthor}>by {ext.author}</span>
                                    </div>
                                </div>
                                <div className="card__body">
                                    <p className={styles.cardDescription}>{ext.description}</p>
                                    <div className={styles.tagContainer}>
                                        {ext.tags.map(tag => (
                                            <span key={tag} className="badge badge--secondary margin-right--xs">{tag}</span>
                                        ))}
                                    </div>
                                </div>
                                <div className="card__footer">
                                    <Link to={ext.permalink} className="button button--primary button--block">
                                        View Details
                                    </Link>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="text--center margin-vert--xl">
                        <h3>No extensions found matching "{searchTerm}"</h3>
                        <p>Try searching for a different keyword.</p>
                    </div>
                )}
            </main>
        </Layout>
    );
}