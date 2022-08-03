import React from 'react';
import {Controller} from "react-hook-form";
import {Form} from "react-bootstrap";

const FIELD_TYPES = ['text', 'email', 'tel', 'date', 'number', 'hidden'];

export default function Input({ field: settings, control, hasError }) {
    return (
        <Controller
            name={settings.id}
            control={control}
            rules={settings.rules}
            render={({ field }) => {
                const type = settings.type && FIELD_TYPES.includes( settings.type ) ? settings.type : 'text';
                return <Form.Control type={type} {...field} {...settings?.bsProps} isInvalid={hasError} />
            }}
        />
    )
}