import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import Api from '../../api';
import { Alert } from 'react-bootstrap';
import {getDefaultValuesByFields} from "../../utils";
import FormFields from "./FormFields";

export default function EditGroup({ group }) {
    let methods = useForm({
        defaultValues: { ...getDefaultValuesByFields(FormFields(), group) }
    });

    const [response, error, loading, axiosFetch] = useAxios();

    const onSubmit = data => {
        axiosFetch({
            axiosInstance: Api,
            method: 'post',
            url: `/groups/${group.id}`,
            data: {name: data.name, description: data.description, template: { id: data.templateId }}
        });
    };

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            {response && !error && <Alert variant="success">Group {group.id} updated</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading} submitLabel="Update" />
        </>
    );
}
