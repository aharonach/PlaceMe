import React from 'react';
import {Breadcrumb} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";
import {useHref} from "react-router-dom";

export default function BreadcrumbTrail({ breadCrumbs }) {
    const location = useHref(window.location.pathname);

    return (
        <Breadcrumb listProps={{ className: "mb-0" }} className="px-3 py-2 rounded-2 my-3 bg-white">
            {breadCrumbs.map(({ match, breadcrumb }) => {
                return <LinkContainer key={match.pathname} to={match.pathname}>
                    <Breadcrumb.Item active={match.pathname.includes(location)}>{breadcrumb}</Breadcrumb.Item>
                </LinkContainer>
            })}
        </Breadcrumb>
    );
}