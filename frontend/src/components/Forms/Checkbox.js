import React from 'react';
import { Form } from "react-bootstrap";
import { Controller } from "react-hook-form";

export default function Checkbox({ settings, formProps }) {
    const error = formProps.formState.errors[settings.id];
    const hasError = !!error;

    return (
        <Form.Group controlId={settings.id} className="mb-3">
            {settings.label && <Form.Label className={hasError ? 'is-invalid' : ''}>{settings.label}</Form.Label>}
            <Controller
                control={formProps.control}
                name={settings.id}
                rules={settings.rules}
                render={({ field }) =>
                {
                    // console.log(field);
                    return <div>
                        {settings.options.map( option => 
                            <Form.Check 
                                key={option.value}
                                id={`${settings.id}-${option.value}`}
                                type={settings.type}
                                label={option.label}
                                checked={field.value && field.value === option.value ? true : null}
                                {...field}
                                value={option.value}
                                {...settings?.bsProps}
                                isInvalid={hasError}
                            />
                        )}
                    </div>}
                }
            />
            {hasError && <div className="invalid-feedback">{error.message ? error.message : "Error"}</div>}
            {settings.description && <Form.Text muted>{settings.description}</Form.Text>}
        </Form.Group>
    )
}
