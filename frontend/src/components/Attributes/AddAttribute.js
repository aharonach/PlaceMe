import React from "react";
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import FormFields from "./FormFields";
import {Alert} from "react-bootstrap";
import Api from '../../api';
import useAxios from "../../hooks/useAxios";

export default function AddAttribute({ templateId, setAttributeList }) {
    let methods = useForm({
        defaultValues: {
            name: '',
            description: '',
            priority: 20,
            type: "range",
        }
    });

    const [template, error, loading, axiosFetch] = useAxios((response) => {
        methods.reset();
        setAttributeList(response.data.attributes);
    });

    const onSubmit = (data) => {
        axiosFetch({
            axiosInstance: Api,
            url: `/templates/${templateId}/attributes`,
            method: 'put',
            data: data,
        });
    }

    return (
        <>
            <h3>Add Attribute</h3>
            {!loading && error && <Alert variant="danger">{error}</Alert> }
            <HtmlForm formProps={methods} fields={FormFields} submitCallback={onSubmit} loading={loading} submitLabel={"Add"}></HtmlForm>
        </>
    );
}