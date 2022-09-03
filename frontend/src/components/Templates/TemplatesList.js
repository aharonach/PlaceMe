import React from 'react';
import RecordList from "../RecordList";
import columns from "./columns";

export default function TemplatesList() {
    return <RecordList
        fetchUrl="/templates"
        propertyName="templateList"
        title={<h1>Templates</h1>}
        addButton="Add Template"
        columns={columns}
        linkField="name"
        sorting={['name', 'createdTime']}
    />
}
