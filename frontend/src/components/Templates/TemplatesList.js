import React from 'react';
import RecordList from "../RecordList";
import columns from "./columns";

export default function TemplatesList() {
    return <RecordList
        fetchUrl="/templates"
        propertyName="templateList"
        linkField="name"
        columns={columns}
        addButton="Add Template"
        title={<h1>Templates</h1>}
    />
}
