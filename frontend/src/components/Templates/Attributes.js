import React, {useState} from 'react';
import TableList from "../TableList";
import AddAttribute from "./Attributes/AddAttribute";
import DeleteAttribute from "./Attributes/DeleteAttribute";
import EditAttribute from "./Attributes/EditAttribute";
import {Button, ButtonGroup, Stack} from "react-bootstrap";
import {useOutletContext} from "react-router-dom";
import FormRange from "react-bootstrap/FormRange";

const mapAttributes = (attribute) => {
    return { ...attribute,
        priority_ui: <Stack direction="horizontal" gap={2}>
            <small>{attribute.priority}</small>
            <FormRange disabled value={attribute.priority} />
        </Stack>
    };
}

export default function Attributes({ addButton = true, actions = true }) {
    const { template } = useOutletContext();
    const [attributeList, setAttributeList] = useState(template.attributes);
    const [editAttribute, setEditAttribute] = useState(null);
    const [mode, setMode] = useState('');

    const columns = {
        name: "Name",
        description: "Description",
        type: "Type",
        priority_ui: "Priority",
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
            <div className="page-header">
                <h2>Attributes</h2>
                <ButtonGroup>
                    {addButton && <Button onClick={() => setMode('add')}>Add Attribute</Button>}
                </ButtonGroup>
            </div>
            <TableList columns={columns} items={attributeList.map(mapAttributes)} nothingToShow={"attributes"} />
            <AddAttribute show={mode === 'add'} setMode={setMode} templateId={template.id} setAttributeList={setAttributeList} />
            {mode === 'edit' && editAttribute && <EditAttribute templateId={template.id} attribute={editAttribute} setAttribute={setEditAttribute} setAttributeList={setAttributeList} />}
        </>
    );
}