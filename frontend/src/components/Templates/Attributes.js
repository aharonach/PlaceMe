import React, {useState} from 'react';
import TableList from "../TableList";
import AddAttribute from "../Attributes/AddAttribute";
import DeleteAttribute from "../Attributes/DeleteAttribute";
import EditAttribute from "../Attributes/EditAttribute";
import {Button} from "react-bootstrap";

export default function Attributes({ template }) {
    const [attributeList, setAttributeList] = useState(template.attributes);
    const [editAttribute, setEditAttribute] = useState(null);

    const columns = {
        name: "Name",
        description: "Description",
        type: "Type",
        priority: "Priority",
        createdTime: "Created Time",
        actions: {
            label: "",
            callbacks: [
                (attribute) => <DeleteAttribute key={`delete-${attribute.id}`} templateId={template.id} attributeList={attributeList} attributeId={attribute.id} setAttributeList={setAttributeList} />,
                (attribute) => <Button key={`edit-${attribute.id}`} size="sm" variant="secondary" onClick={() => setEditAttribute(attribute)}>Edit</Button>
            ]
        }
    };

    return (
        <>
            <h2>Attributes</h2>
            <AddAttribute templateId={template.id} setAttributeList={setAttributeList} />
            <TableList basePath={`/templates/${template.id}/attributes/`} columns={columns} items={attributeList} />
            {editAttribute && <EditAttribute templateId={template.id} attribute={editAttribute} setAttribute={setEditAttribute} setAttributeList={setAttributeList} />}
        </>
    );
}