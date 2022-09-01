import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import { Alert } from 'react-bootstrap';
import {getDefaultValuesByFields} from "../../utils";
import FormFields from "./FormFields";
import {useNavigate, useOutletContext} from "react-router-dom";

export default function EditTemplate() {
    const {template, error, loading, axiosFetch} = useOutletContext();

    let methods = useForm({
        defaultValues: { ...getDefaultValuesByFields(FormFields(), template) }
    });

    const navigate = useNavigate();

    const onSubmit = data => {
        axiosFetch({
            method: 'post',
            url: `/templates/${template.id}`,
            data: {...data}
        }).then( template => template && navigate(`/templates/${template.id}`, { replace: true }));
    };

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading} submitLabel="Update" />
        </>
    );
}
