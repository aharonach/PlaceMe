import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import Api from '../../api';
import { Alert } from 'react-bootstrap';
import FormFields from './FormFields';
import {getDefaultValuesByFields} from "../../utils";

export default function EditPlacement({ placement }){

    const [response, error, loading, axiosFetch] = useAxios();

    let methods = useForm({
        defaultValues: { ...getDefaultValuesByFields(FormFields(), placement) }
    });

    const onSubmit = data => {
        data.group = {id: data.groupId};

        axiosFetch({
            axiosInstance: Api,
            method: 'post',
            url: `/placements/${placement.id}`,
            data: {...data}
        });
    };

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            {response && !error && <Alert variant="success">Placement {placement.id} updated</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading} submitLabel="Update" />
        </>
    );
}