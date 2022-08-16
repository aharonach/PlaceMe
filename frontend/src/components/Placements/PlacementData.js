import React from 'react';
import PlacementResultsList from "./PlacementResultsList";


export default function PlacementData({placement}){


    return (
        <>
            <p>
                <div><strong>Created on:</strong> {placement.createdTime}</div>
                <div><strong>Number Of Classes:</strong> {placement.numberOfClasses}</div>
                <div><strong>Group:</strong> {placement.groupId ? placement.group.name : "No group"}</div>
            </p>

            <p>
                <h3>Results:</h3>
                <PlacementResultsList placement={placement} />
            </p>
        </>
    );
}