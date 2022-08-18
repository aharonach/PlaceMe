import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import Api from '../../api';
import { Alert } from 'react-bootstrap';
import FormFields from './FormFields';
import {useNavigate, useParams} from "react-router-dom";
import useFetchRecord from "../../hooks/useFetchRecord";
import {setFormValues} from "../../utils";

export default function EditPupil() {
    const { pupilId } = useParams();
    const navigate = useNavigate();
    let methods = useForm();
    const [pupil, error, loading, axiosFetch] = useFetchRecord(
        `/pupils/${pupilId}`,
        res => setFormValues(methods, res)
    );

    const onSubmit = data => {
        axiosFetch({
            axiosInstance: Api,
            method: 'post',
            url: `/pupils/${pupilId}`,
            data: {...data}
        }).then(pupil => pupil && navigate(`/pupils/${pupil.id}`, { replace: true }));
    };

    return (
        pupil && <>
            <h2>Edit {pupil.firstName} {pupil.lastName}</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading} submitLabel="Update" />
        </>
    );
}
