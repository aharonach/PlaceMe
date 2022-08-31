import React from 'react';
import RecordList from "../RecordList";
import columns from "./columns";

export default function GroupsList() {
    return <RecordList
            fetchUrl="/groups/"
            propertyName="groupList"
            title={<h1>Groups</h1>}
            addButton="Add Group"
            columns={columns}
            linkField="name"
        />;
}
