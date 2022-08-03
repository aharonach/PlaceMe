import React from "react";
import {Button, Col, Form, Spinner} from "react-bootstrap";
import Select from "./Select";
import Input from "./Input";
import Checkbox from './Checkbox';
import Checkboxes from './Checkboxes';
import Textarea from "./Textarea";
import Label from "./Label";
import FieldFeedback from "./FieldFeedback";

export default function HtmlForm({ fields, submitCallback, formProps, loading, submitLabel, vertical, children }) {
    const formFields = 'function' === typeof ( fields ) ? fields() : fields;

    return (
        <Form onSubmit={formProps.handleSubmit(submitCallback)}>
            {formFields.map( field => {
                const error = formProps.formState.errors[field.id];
                const hasError = !!error;

                return (
                    <Form.Group key={field.id} as={vertical ? Col : "div"} controlId={field.id} className="mb-3">
                        <Label settings={field} />
                        {outputField(field, { field: field, control: formProps.control, hasError: hasError })}
                        {<FieldFeedback hasError={hasError} error={error} />}
                        {field.description && <Form.Text muted>{field.description}</Form.Text>}
                    </Form.Group>
                )
            })}
            {children}
            <Button type="submit" variant="primary">
                {loading && <Spinner
                    as="span"
                    animation="border"
                    role="status"
                    size="sm"
                    aria-hidden="true"
                />}
                {submitLabel ? submitLabel : 'Submit'}
            </Button>
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
            return <Select {...props} />;

        case 'textarea':
            return <Textarea {...props} />;

        default:
            return <Input {...props} />;
    }
}