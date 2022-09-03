import React, {useState} from 'react';
import TableList from "../TableList";
import AddAttribute from "./Attributes/AddAttribute";
import DeleteAttribute from "./Attributes/DeleteAttribute";
import EditAttribute from "./Attributes/EditAttribute";
import {Button, ButtonGroup} from "react-bootstrap";
import {useOutletContext} from "react-router-dom";

export default function Attributes({ addButton = true, actions = true }) {
    const { template } = useOutletContext();
    const [attributeList, setAttributeList] = useState(template.attributes);
    const [editAttribute, setEditAttribute] = useState(null);
    const [mode, setMode] = useState('');

    const columns = {
        name: "Name",
        description: "Description",
        type: "Type",
        priority: "Priority",
        createdTime: "Created Time",
    };

    const setEditMode = (attribute) => {
        setEditAttribute(attribute);
        setMode('edit');
    }

    if ( actions ) {
        columns.actions = {
            label: "",
            callbacks: [
                (attribute) => <DeleteAttribute key={`delete-${attribute.id}`} templateId={template.id} attributeList={attributeList} attributeId={attribute.id} setAttributeList={setAttributeList} />,
                (attribute) => <Button key={`edit-${attribute.id}`} size="sm" variant="secondary" onClick={() => setEditMode(attribute)}>Edit</Button>
            ]
        }
    }

    return (
        <>
            <h2>Attributes</h2>
            <ButtonGroup className="mb-3">
                {addButton && <Button onClick={() => setMode('add')}>Add Attribute</Button>}
            </ButtonGroup>
            <TableList columns={columns} items={attributeList} />
            <AddAttribute show={mode === 'add'} setMode={setMode} templateId={template.id} setAttributeList={setAttributeList} />
            {mode === 'edit' && editAttribute && <EditAttribute templateId={template.id} attribute={editAttribute} setAttribute={setEditAttribute} setAttributeList={setAttributeList} />}
        </>
    );
}