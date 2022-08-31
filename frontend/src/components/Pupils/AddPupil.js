import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import Api from '../../api';
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
            axiosInstance: Api,
            method: 'put',
            url: '/pupils',
            data: {...data}
        }).then((pupil) => {
            pupil && navigate(`/pupils/${pupil.id}`);
        });
    };

    return (
        <>
            <h1>Add Pupil</h1>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading}></HtmlForm>
        </>
    );
}
