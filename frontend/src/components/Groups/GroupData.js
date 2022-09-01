import {Link, useOutletContext} from "react-router-dom";
import {idLinkList} from "../../utils";
import React from "react";
import RecordDetails from "../RecordDetails";
import {PupilList} from "./index";

export default function GroupData() {
    const {group} = useOutletContext();

    const details = [
        { label: "Name", value: group.name },
        { label: "Number of pupils", value: group.numberOfPupils },
        { label: "Description", value: group.description },
        { label: "Template ID", value: group.templateId && <Link to={`/templates/${group.templateId}`}>{group.templateId}</Link> },
        { label: "Placement IDs", value: idLinkList('placements', group.placementIds ) },
        { label: "Created Time", value: group.createdTime },
    ];

    return (
        <>
            <RecordDetails details={details} />
            <PupilList />
        </>
    )
}