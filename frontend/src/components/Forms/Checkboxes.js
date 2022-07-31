import React, {useState} from 'react';
import { Form } from "react-bootstrap";
import {Controller, useController} from "react-hook-form";

export default function Checkbox({ settings, formProps }) {
    const error = formProps.formState.errors[settings.id];
    const hasError = !!error;

    const name = settings.id;
    const control = formProps.control;
    const { field } = useController({ control, name });

    const [value, setValue] = useState(Array.isArray( field.value ) ? field.value.map( v => v.value ) : [] );

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
                            {settings.options && settings.options.map( (option, index) => {
                                    const checked = field.value &&
                                        (
                                            ( Array.isArray(field.value) && field.value.find( val => val.value === option.value ) )
                                            || field.value === option.value
                                        );

                                    return (
                                        <Form.Check
                                            key={option.value}
                                            id={`${settings.id}-${option.value}`}
                                            type={settings.type}
                                            label={option.label}
                                            checked={checked ? true : null}
                                            {...field}
                                            onChange={(e) => {
                                                let valueCopy = [...value];

                                                // update checkbox value
                                                valueCopy[index] = e.target.checked ? e.target.value : null;
                                                valueCopy = valueCopy.filter(Boolean);

                                                // send data to react hook form
                                                field.onChange(valueCopy);

                                                // update local state
                                                setValue(valueCopy);
                                            }}
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
