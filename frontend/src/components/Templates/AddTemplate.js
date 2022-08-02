import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import Api from '../../api';
import { Alert } from 'react-bootstrap';
import { useNavigate } from "react-router-dom";
import FormFields from "./FormFields";

export default function AddTemplate() {
    const [template, error, loading, axiosFetch] = useAxios();

    let methods = useForm({
        defaultValues: {
            name: '',
            description: '',
            attributes: [],
        }
    });

    let navigate = useNavigate();

    const onSubmit = (data) => {
        axiosFetch({
            axiosInstance: Api,
            method: 'put',
            url: '/templates',
            data: {...data}
        });
    };

    template && !error && navigate(`/templates/${template.id}`);

    return (
        <>
            <h2>Add Template</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading}></HtmlForm>
        </>
    );
}
