import React from "react";
import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import FormFields from "./FormFields";
import useAxios from "../../hooks/useAxios";

export default function AddAttribute({ template }) {
    let methods = useForm({
        defaultValues: {
            name: '',
            description: '',
            priority: 20,
        }
    });

    const [attribute, error, loading, axiosFetch] = useAxios();

    const onSubmit = (data) => {
        console.log(data);
    }

    return (
        <>
            <h2>Add Attribute to Template {template.id}</h2>
            <HtmlForm formProps={methods} fields={FormFields} submitCallback={onSubmit} loading={loading}></HtmlForm>
        </>
    );
}