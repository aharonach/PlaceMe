import React from 'react';
import {Form} from "react-bootstrap";

export default function Label({ settings }) {
    return (
        settings.label && <Form.Label>{settings.label} {settings?.rules?.required ? <abbr title="required">*</abbr> : ''}</Form.Label>
    )
}