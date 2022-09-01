import React, {useContext} from 'react';
import {Breadcrumb} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";
import RecordContext from "../context/RecordContext";

export default function BreadcrumbTrail({ breadCrumbs}) {
    // const { record } = useContext(RecordContext);

    return (
        <Breadcrumb>
            {breadCrumbs.map(({ match, breadcrumb }) => {
                let display = breadcrumb;
                //
                // if ( record.record ) {
                //     if ( record.pathname === match.pathname ) {
                //         display = '';
                //         record.displayFields.forEach(displayField => display += record.record[displayField] + " ");
                //         display.trimEnd();
                //     }
                // }

                return <LinkContainer key={match.pathname} to={match.pathname}><Breadcrumb.Item>{display}</Breadcrumb.Item></LinkContainer>
            })}
        </Breadcrumb>
    );
}