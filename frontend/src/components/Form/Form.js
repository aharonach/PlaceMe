import React from "react";
import {Button, Form as BSForm} from "react-bootstrap";
import Select from "./Select";
import Input from "./Input";

export default function Form({ fields, submitCallback, formProps }) {
    return (
        <BSForm onSubmit={formProps.handleSubmit(submitCallback)}>
            {fields.map( field => {
                switch(field.type) {
                    case 'select':
                        return <Select key={field.id} settings={field} formProps={formProps} />;

                    default:
                        return <Input key={field.id} settings={field} formProps={formProps} />;
                }
            })}
            <Button type="submit" variant="primary">Submit</Button>
        </BSForm>
    );
}