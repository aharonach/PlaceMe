import React from 'react';
import RecordDetails from "../RecordDetails";

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