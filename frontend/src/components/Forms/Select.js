import React from "react";
import {Form} from "react-bootstrap";
import {Controller} from "react-hook-form";

export default function Select({ settings, formProps }) {
    const error = formProps.formState.errors[settings.id];
    const hasError = !!error;

    return (
        <Form.Group controlId={settings.id} className="mb-3">
            {settings.label && <Form.Label>{settings.label}</Form.Label>}
            <Controller
                name={settings.id}
                control={formProps.control}
                rules={settings.rules}
                render={({ field }) => {
                    return (
                        <Form.Select {...field} {...settings?.bsProps} isInvalid={hasError}>
                            {settings.options.map(option => (
                                <option
                                    key={option.value}
                                    value={option.value}
                                    selected={field.value && field.value === option.value ? true : null}
                                >
                                    {option.label}
                                </option>
                            ))}
                        </Form.Select>
                    );
                }}
            />
            {hasError && <div className="invalid-feedback">{error.message ? error.message : "Error"}</div>}
            {settings.description && <Form.Text muted>{settings.description}</Form.Text>}
        </Form.Group>
    )
}