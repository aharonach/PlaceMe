import React from 'react';
import PlacementResultsList from "./PlacementResultsList";
import RecordDetails from "../RecordDetails";
import {Button} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";


export default function PlacementData({placement}){

    const details = placement && [
        { label: "Number Of Classes", value: placement.numberOfClasses },
        { label: "Group", value: placement.groupId ? placement.group.name : "No group" },
        { label: "Created On", value: placement.createdTime },
    ];

    return (
        <>
            <RecordDetails numOfColumns={3} details={details} />

            {/*<p>*/}
            {/*    <div><strong>Created on:</strong> {placement.createdTime}</div>*/}
            {/*    <div><strong>Number Of Classes:</strong> {placement.numberOfClasses}</div>*/}
            {/*    <div><strong>Group:</strong> {placement.groupId ? placement.group.name : "No group"}</div>*/}
            {/*</p>*/}

            <p>

                <h3>Result:</h3>
                
                {/*<PlacementResultsList placement={placement} />*/}
            </p>
        </>
    );
}