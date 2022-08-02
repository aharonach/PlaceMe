import React from 'react';
import {Controller} from "react-hook-form";
import {Form} from "react-bootstrap";
import Label from "./Label";
import FieldFeedback from "./FieldFeedback";

const FIELD_TYPES = ['text', 'email', 'tel', 'date', 'number'];

export default function Input({ settings, formProps }) {
    const error = formProps.formState.errors[settings.id];
    const hasError = !!error;

    return (
        <Form.Group controlId={settings.id} className="mb-3">
            <Label settings={settings} />
            <Controller
                name={settings.id}
                control={formProps.control}
                rules={settings.rules}
                render={({ field }) => {
                    const type = settings.type && FIELD_TYPES.includes( settings.type ) ? settings.type : 'text';
                    return <Form.Control type={type} {...field} {...settings?.bsProps} isInvalid={hasError} />
                }}
            />
            <FieldFeedback hasError={hasError} error={error} />
            {settings.description && <Form.Text muted>{settings.description}</Form.Text>}
        </Form.Group>
    )
}