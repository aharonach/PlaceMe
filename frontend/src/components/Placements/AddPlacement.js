import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import Api from '../../api';
import { Alert } from 'react-bootstrap';
import { useNavigate } from "react-router-dom";
import FormFields from "./FormFields";

export default function AddPlacement(){

    const [placement, error, loading, axiosFetch] = useAxios();

    let methods = useForm({
        defaultValues: {
            name: '',
            numberOfClasses: '2',
            groupId: '',
        }
    });

    let navigate = useNavigate();

    const onSubmit = (data) => {
        data.group = {id: data.groupId};

        axiosFetch({
            axiosInstance: Api,
            method: 'put',
            url: '/placements',
            data: {...data}
        }).then((placement) => {
            placement && navigate(`/placements/${placement.id}`);
        });
    };

    return (
        <>
            <h2>Add Placement</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm fields={FormFields} formProps={methods} submitCallback={onSubmit} loading={loading}></HtmlForm>
        </>
    );
}