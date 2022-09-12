import React from "react";
import {Button, Col, Form, Row, Stack} from "react-bootstrap";
import Select from "./Select";
import Input from "./Input";
import Checkbox from './Checkbox';
import Checkboxes from './Checkboxes';
import Textarea from "./Textarea";
import Label from "./Label";
import FieldFeedback from "./FieldFeedback";
import Loading from "../Loading";
import Range from "./Range";
import SelectMultiple from "./SelectMultiple";
import File from "./File";

function outputFieldWrapper(field, formProps, hasError, error) {
    const output = outputField(field, {field: field, control: formProps.control, hasError: hasError});

    if ( field.type === 'hidden' ) {
        return output;
    }

    return (
        <Form.Group as="div" controlId={field.id} className="mb-3">
            <Label settings={field}/>
            {output}
            {field.description && <Form.Text muted>{field.description}</Form.Text>}
            {<FieldFeedback hasError={hasError} error={error}/>}
        </Form.Group>
    );
}

export default function HtmlForm(
    {
        fields,
        submitCallback,
        formProps,
        loading,
        submitLabel,
        disabled = false,
        submitClass,
        additionalButtons,
        children,
        rows = 1
}) {
    const formFields = 'function' === typeof ( fields ) ? fields() : fields;

    return (
        <Form onSubmit={!disabled ? formProps.handleSubmit(submitCallback) : () => {}}>
            <Row md={rows}>
                {formFields.map( field => {
                    const error = formProps.formState.errors[field.id];
                    const hasError = !!error;

                    if ( disabled ) {
                        field.bsProps.disabled = true;
                    }

                    const output = outputFieldWrapper(field, formProps, hasError, error);

                    if ( field.type === 'hidden' ) {
                        return output;
                    }

                    return <Col key={field.id}>{output}</Col>;
                })}
            </Row>

            {children}
            {!disabled && (
                <Stack direction="horizontal" gap={2} className="align-items-center flex-wrap">
                    <Button type="submit" variant="primary" className={submitClass}>
                        {!additionalButtons && <Loading show={loading} size="sm" block={false} />}
                        {submitLabel ? submitLabel : 'Submit'}
                    </Button>
                    {additionalButtons}
                    {additionalButtons && <Loading show={loading} size="sm" block={false} />}
                </Stack>
            )}
        </Form>
    );
}

function outputField(field, props) {
    switch(field.type) {
        case 'checkbox':
            if ( Array.isArray( field.options ) ) {
                return <Checkboxes {...props} />
            }
        // eslint-disable-next-line no-fallthrough
        case 'radio':
            return <Checkbox {...props} />;

        case 'select':
            if ( field.multiple ) {
                return <SelectMultiple {...props} />;
            }

            return <Select {...props} />;

        case 'textarea':
            return <Textarea {...props} />;

        case 'range':
            return <Range {...props} />;

        case 'file':
            return <File {...props} />;

        default:
            return <Input key={field.id} {...props} />;
    }
}