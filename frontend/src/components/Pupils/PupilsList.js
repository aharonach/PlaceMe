import React from 'react';
import RecordList from "../RecordList";
import columns from "./columns";

export default function PupilsList() {
    return <RecordList
        fetchUrl="/pupils/"
        propertyName="pupilList"
        title={<h1>Pupils</h1>}
        addButton="Add Pupil"
        columns={columns}
        linkField="givenId"
        sorting={['firstName', 'lastName', 'givenId', 'createdTime', 'gender', 'birthDate']}
    />
}
