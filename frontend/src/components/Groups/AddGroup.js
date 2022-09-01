import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import { Alert } from 'react-bootstrap';
import { useNavigate } from "react-router-dom";
import FormFields from "./FormFields";

export default function AddGroup() {
    const [group, error, loading, axiosFetch] = useAxios();

    let methods = useForm({
        defaultValues: {
            name: '',
            description: '',
            templateId: '',
        }
    });

    let navigate = useNavigate();

    const onSubmit = (data) => {
        axiosFetch({
            method: 'put',
            url: '/groups',
            data: {name: data.name, description: data.description, template: { id: data.templateId }}
        }).then((group) => group && navigate(`/groups/${group.id}`));
    };

    return (
        <>
            <h1>Add Group</h1>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading}></HtmlForm>
        </>
    );
}
