import type { ReactNode } from "react";
import clsx from "clsx";
import Heading from "@theme/Heading";
import styles from "./styles.module.css";

type FeatureItem = {
    title: string;
    Svg: React.ComponentType<React.ComponentProps<"svg">>;
    description: ReactNode;
};

const FeatureList: FeatureItem[] = [
    {
        title: "Team Chat & Roles",
        Svg: require("@site/static/img/chat.svg").default,
        description: (
            <>
                Collaborate seamlessly with private chat and control permissions with team roles such as owner and
                admin.
            </>
        ),
    },
    {
        title: "Team Homes & Warps",
        Svg: require("@site/static/img/home.svg").default,
        description: (
            <>Set your team's home, teleport fast. Configure named warps for your team to save important locations.</>
        ),
    },
    {
        title: "Simple Setup, Powerful Control & Translation",
        Svg: require("@site/static/img/settings.svg").default,
        description: (
            <>
                Intuitive commands and full config for smooth server integration. Supporting over 20 languages, or if
                your language is missing, every message can be translated.
            </>
        ),
    },
];

function Feature({ title, Svg, description }: FeatureItem) {
    return (
        <div className={clsx("col col--4")}>
            <div className="text--center">
                <Svg className={styles.featureSvg} role="img" />
            </div>
            <div className="text--center padding-horiz--md">
                <Heading as="h3">{title}</Heading>
                <p>{description}</p>
            </div>
        </div>
    );
}

export default function HomepageFeatures(): ReactNode {
    return (
        <section className={styles.features}>
            <div className="container">
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    );
}

