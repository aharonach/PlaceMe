import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import { Alert } from 'react-bootstrap';
import FormFields from './FormFields';
import {getDefaultValuesByFields} from "../../utils";
import {useNavigate, useOutletContext} from "react-router-dom";

export default function EditPlacement(){
    const { placement, error, loading, axiosFetch } = useOutletContext();
    const navigate = useNavigate();

    let methods = useForm({
        defaultValues: { ...getDefaultValuesByFields(FormFields(), placement) }
    });

    const onSubmit = data => {
        axiosFetch({
            method: 'post',
            url: `/placements/${placement.id}`,
            data: {...data, group: { id: data.groupId }}
        }).then(placement => placement && navigate(`/placements/${placement.id}`, { replace: true }));
    };

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading} submitLabel="Update" />
        </>
    );
}