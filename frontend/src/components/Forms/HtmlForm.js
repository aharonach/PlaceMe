import React from "react";
import {Button, Form as BSForm, Spinner} from "react-bootstrap";
import Select from "./Select";
import Input from "./Input";
import Checkbox from './Checkbox';
import Checkboxes from './Checkboxes';
import Textarea from "./Textarea";

export default function HtmlForm({ fields, submitCallback, formProps, loading, submitLabel, children }) {
    const formFields = 'function' === typeof ( fields ) ? fields() : fields;

    return (
        <BSForm onSubmit={formProps.handleSubmit(submitCallback)}>
            {formFields.map( field => {
                switch(field.type) {
                    case 'checkbox':
                        if ( Array.isArray( field.options ) ) {
                            return <Checkboxes key={field.id} settings={field} formProps={formProps} />
                        }
                    // eslint-disable-next-line no-fallthrough
                    case 'radio':
                        return <Checkbox key={field.id} settings={field} formProps={formProps} />;

                    case 'select':
                        return <Select key={field.id} settings={field} formProps={formProps} />;

                    case 'repeater':
                        return '';

                    case 'textarea':
                        return <Textarea key={field.id} settings={field} formProps={formProps} />;

                    default:
                        return <Input key={field.id} settings={field} formProps={formProps} />;
                }
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
        </BSForm>
    );
}