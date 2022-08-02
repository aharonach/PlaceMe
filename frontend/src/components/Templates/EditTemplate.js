import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import Api from '../../api';
import { Alert } from 'react-bootstrap';
import {getDefaultValuesByFields} from "../../utils";
import FormFields from "./FormFields";

export default function EditTemplate({ template }) {
    const [response, error, loading, axiosFetch] = useAxios();

    let methods = useForm({
        defaultValues: { ...getDefaultValuesByFields(FormFields(), template) }
    });

    const onSubmit = data => {
        axiosFetch({
            axiosInstance: Api,
            method: 'post',
            url: `/template/${template.id}`,
            data: {...data}
        });
    };

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            {response && !error && <Alert variant="success">Template {template.id} updated</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading} submitLabel="Update" />
        </>
    );
}
