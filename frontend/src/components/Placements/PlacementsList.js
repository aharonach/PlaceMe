import React from 'react';
import columns from "./columns";
import RecordList from "../RecordList";

export default function PlacementsList(){
    return <RecordList
        fetchUrl="/placements/"
        propertyName="placementList"
        title={<h1>Placements</h1>}
        addButton="Add Placement"
        columns={columns}
        linkField="name"
        sorting={['name', 'numberOfClasses', 'createdTime']}
    />
}
