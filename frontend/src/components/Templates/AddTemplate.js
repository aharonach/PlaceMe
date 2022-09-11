import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import { Alert } from 'react-bootstrap';
import { useNavigate } from "react-router-dom";
import FormFields from "./FormFields";

export default function AddTemplate() {
    // eslint-disable-next-line no-unused-vars
    const [ template, error, loading, axiosFetch] = useAxios();

    let methods = useForm({
        defaultValues: {
            name: '',
            description: '',
            attributes: [],
        }
    });

    const navigate = useNavigate();

    const onSubmit = (data) => {
        axiosFetch({
            method: 'put',
            url: '/templates',
            data: {...data}
        }).then( template => template && navigate(`/templates/${template.id}`, { replace: true }));
    };

    return (
        <>
            <div className={"page-header"}>
                <h1>Add Template</h1>
            </div>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading}></HtmlForm>
        </>
    );
}
