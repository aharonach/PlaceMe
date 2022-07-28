import React from "react";
import {Form} from "react-bootstrap";
import {Controller} from "react-hook-form";

export default function Select({ field: settings, formProps, bsProps = {} }) {
    return (
        <Form.Group controlId={settings.id} class="mb-3">
            {settings.label && <Form.Label>{settings.label}</Form.Label>}
            <Controller
                name={settings.id}
                control={formProps.control}
                rules={settings.rules}
                render={({ fieldProps }) => {
                    return (
                        <Form.Select {...fieldProps} {...bsProps}>
                            {settings.options.map(option => (
                                <option value={option.value}>{option.label}</option>
                            ))}
                        </Form.Select>
                    );
                }}
            />
            {settings.description && <Form.Text muted>{settings.description}</Form.Text>}
        </Form.Group>
    )
}