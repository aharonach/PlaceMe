import {Link, useOutletContext} from "react-router-dom";
import {humanizeTime, idLinkList} from "../../utils";
import React from "react";
import RecordDetails from "../RecordDetails";
import {Pupils} from "./index";

export default function GroupData() {
    const {group} = useOutletContext();

    const details = [
        { label: "Number of pupils", value: group.numberOfPupils },
        { label: "Description", value: group.description },
        { label: "Template ID", value: group.templateId && <Link to={`/templates/${group.templateId}`}>{group.templateId}</Link> },
        { label: "Placement IDs", value: idLinkList('placements', group.placementIds ) },
        { label: "Created Time", value: humanizeTime(group.createdTime) },
    ];

    return (
        <>
            <RecordDetails details={details} />
            <Pupils />
        </>
    )
}