import React from "react";
import {Form} from "react-bootstrap";
import {Controller} from "react-hook-form";
import Label from "./Label";
import FieldFeedback from "./FieldFeedback";

export default function Select({ settings, formProps }) {
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
            <FieldFeedback hasError={hasError} error={error} />
            {settings.description && <Form.Text muted>{settings.description}</Form.Text>}
        </Form.Group>
    )
}