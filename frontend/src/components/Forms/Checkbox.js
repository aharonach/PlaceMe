import React from 'react';
import { Form } from "react-bootstrap";
import {Controller} from "react-hook-form";

export default function Checkbox({ settings, formProps }) {
    const error = formProps.formState.errors[settings.id];
    const hasError = !!error;

    const name = settings.id;
    const control = formProps.control;

    return (
        <Form.Group controlId={settings.id} className="mb-3">
            {settings.label && <Form.Label>{settings.label}</Form.Label>}
            <Controller
                control={control}
                name={name}
                rules={settings.rules}
                render={({ field } ) => {
                    return (
                        <div className={hasError ? 'is-invalid' : ''}>
                            {settings.options && settings.options.map( option => {
                                const checked = field.value &&
                                    (
                                        field.value === option.value ||
                                        ( Array.isArray(field.value) && field.value.find( val => val.value === option.value ) )
                                    );

                                return (
                                        <Form.Check
                                            key={option.value}
                                            id={`${settings.id}-${option.value}`}
                                            type={settings.type}
                                            label={option.label}
                                            checked={checked ? true : null}
                                            {...field}
                                            value={option.value}
                                            {...settings?.bsProps}
                                            isInvalid={hasError}
                                        />
                                    )
                                }
                            )}
                        </div>
                    )
                }}
            />
            {hasError && <div className="invalid-feedback">{error.message ? error.message : "Error"}</div>}
            {settings.description && <Form.Text muted>{settings.description}</Form.Text>}
        </Form.Group>
    )
}
