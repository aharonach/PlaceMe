import React from 'react';
import {Controller} from "react-hook-form";
import {Form} from "react-bootstrap";

const FIELD_TYPES = ['text', 'email', 'tel', 'date', 'number'];

export default function Input({ settings, formProps, bsProps = {} }) {
    const error = formProps.formState.errors[settings.id];

    console.log(error);

    return (
        <Form.Group controlId={settings.id} className="mb-3">
            {settings.label && <Form.Label>{settings.label}</Form.Label>}
            <Controller
                name={settings.id}
                control={formProps.control}
                rules={settings.rules}
                render={({ field }) => {
                    const type = settings.type && FIELD_TYPES.includes( settings.type ) ? settings.type : 'text';
                    return <Form.Control type={type} {...field} {...bsProps} isInvalid={!!error} />
                }}
            />
            {error && <div className="invalid-feedback">{error.message ? error.message : "Error"}</div>}
            {settings.description && <Form.Text muted>{settings.description}</Form.Text>}
        </Form.Group>
    )
}