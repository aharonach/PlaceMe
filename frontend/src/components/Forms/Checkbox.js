import React from 'react';
import { Form } from "react-bootstrap";
import {Controller} from "react-hook-form";

export default function Checkbox({ settings, formProps }) {
    const error = formProps.formState.errors[settings.id];
    const hasError = !!error;

    return (
        <Form.Group controlId={settings.id} className="mb-3">
            {settings.label && <Form.Label>{settings.label}</Form.Label>}
            <div className={hasError ? 'is-invalid' : ''}>
                {settings.options && settings.options.map( option =>
                    <Controller
                        key={option.value}
                        control={formProps.control}
                        name={settings.id}
                        rules={settings.rules}
                        render={({ field } ) =>
                            <Form.Check
                                id={`${settings.id}-${option.value}`}
                                type={settings.type}
                                label={option.label}
                                defaultChecked={field.value && field.value === option.value}
                                {...field}
                                value={option.value}
                                {...settings?.bsProps}
                                isInvalid={hasError}
                            />
                        }
                    />
                )}
            </div>
            {hasError && <div className="invalid-feedback">{error.message ? error.message : "Error"}</div>}
            {settings.description && <Form.Text muted>{settings.description}</Form.Text>}
        </Form.Group>
    );
}
