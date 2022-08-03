import React from 'react';
import {Controller} from "react-hook-form";
import {Form} from "react-bootstrap";

export default function Textarea({ field: settings, control, hasError }) {
    return (
        <Controller
            name={settings.id}
            control={control}
            rules={settings.rules}
            render={({ field }) => {
                return <Form.Control as="textarea" {...field} {...settings?.bsProps} isInvalid={hasError} />
            }}
        />
    )
}