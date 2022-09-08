import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import { Alert } from 'react-bootstrap';
import { useNavigate } from "react-router-dom";
import FormFields from "./FormFields";

export default function AddPupil() {
    const [pupil, error, loading, axiosFetch] = useAxios();

    let methods = useForm({
        defaultValues: {
            givenId: '',
            firstName: '',
            lastName: '',
            birthDate: '',
            gender: 'MALE',
        }
    });

    let navigate = useNavigate();

    const onSubmit = (data) => {
        axiosFetch({
            method: 'put',
            url: '/pupils',
            data: {
                ...data,
                groups: data.groupIds?.map( groupId => Object.assign({}, { id: groupId }))
            }
        }).then(pupil => pupil && navigate(`/pupils/${pupil.id}`, { replace: true }));
    };

    return (
        <>
            <h1>Add Pupil</h1>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading}></HtmlForm>
        </>
    );
}
