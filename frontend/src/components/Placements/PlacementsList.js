import React from 'react';
import columns from "./columns";
import RecordList from "../RecordList";
import {Link} from "react-router-dom";

const mapPlacements = (placement) => {
    return { ...placement,
        groupId: placement.groupId
            ? <Link to={`/groups/${placement.groupId}`}>{placement.group.name}</Link>
            : <Link to="edit">Assign to a group</Link>,
        numberOfResults: <Link to={`/placements/${placement.id}/results`}>{placement.numberOfResults}</Link>
    }
}

export default function PlacementsList(){
    return <RecordList
        fetchUrl="/placements/"
        propertyName="placementList"
        title={<h1>Placements</h1>}
        addButton="Add Placement"
        columns={columns}
        mapCallback={mapPlacements}
        linkField="name"
        sorting={['name', 'numberOfClasses', 'createdTime']}
    />
}
