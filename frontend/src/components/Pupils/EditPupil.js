import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import { Alert } from 'react-bootstrap';
import FormFields from './FormFields';
import {useNavigate, useOutletContext} from "react-router-dom";
import {getDefaultValuesByFields} from "../../utils";

export default function EditPupil() {
    const { pupil, error, loading, axiosFetch } = useOutletContext();
    const navigate = useNavigate();
    let methods = useForm({
        defaultValues: { ...getDefaultValuesByFields(FormFields(), pupil) }
    });

    const onSubmit = data => {
        axiosFetch({
            method: 'post',
            url: `/pupils/${pupil.id}`,
            data: {...data}
        }).then(pupil => pupil && navigate(`/pupils/${pupil.id}`, { replace: true }));
    };

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            {/*{pupil && !error && <Alert variant="success">Pupil {pupil.firstName} {pupil.lastName} ({pupil.id}) updated</Alert>}*/}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading} submitLabel="Update" />
        </>
    );
}
