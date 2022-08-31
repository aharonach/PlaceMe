import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import { Alert } from 'react-bootstrap';
import {getDefaultValuesByFields} from "../../utils";
import FormFields from "./FormFields";
import {useMatch, useNavigate, useOutletContext} from "react-router-dom";

export default function EditGroup() {
    const {group, loading, error, axiosFetch} = useOutletContext();
    const navigate = useNavigate();
    let methods = useForm({
        defaultValues: { ...getDefaultValuesByFields(FormFields(), group) }
    });

    const onSubmit = data => {
        axiosFetch({
            method: 'post',
            url: `/groups/${group.id}`,
            data: {name: data.name, description: data.description, template: { id: data.templateId }}
        }).then(group => group && navigate(`/groups/${group.id}`, { replace: true }));
    };

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading} submitLabel="Update" />
        </>
    );
}
