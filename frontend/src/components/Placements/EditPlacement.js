import React from 'react';
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import useAxios from "../../hooks/useAxios";
import Api from '../../api';
import { Alert } from 'react-bootstrap';
import FormFields from './FormFields';
import {getDefaultValuesByFields} from "../../utils";
import { useNavigate } from "react-router-dom";

//export default function EditPlacement({placement, error, loading, axiosFetch}){
export default function EditPlacement({placement, count, setCount}){

    const [response, error, loading, axiosFetch] = useAxios();

    let navigate = useNavigate();

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
        }).then((placement) => {
            setCount(count + 1);
            placement && navigate(`/placements/${placement.id}`);
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