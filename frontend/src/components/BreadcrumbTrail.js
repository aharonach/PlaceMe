import React from 'react';
import {Breadcrumb} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";

export default function BreadcrumbTrail({ breadCrumbs }) {
    return (
        <Breadcrumb listProps={{ className: "mb-0" }} className="px-3 py-2 rounded-2 my-3 bg-white">
            {breadCrumbs.map(({ match, breadcrumb }) => {
                return <LinkContainer key={match.pathname} to={match.pathname}><Breadcrumb.Item>{breadcrumb}</Breadcrumb.Item></LinkContainer>
            })}
        </Breadcrumb>
    );
}