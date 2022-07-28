import React from 'react';
import {useForm} from "react-hook-form";
import Form from "../Form/Form";

export default function AddPupil() {
    const onSubmit = data => console.log(data);

    let methods = useForm({
        defaultValues: {
            givenId: '',
            firstName: '',
            lastName: '',
            birthDate: '',
        }
    });

    const fields = [
        {
            id: 'givenId',
            label: 'Given ID',
            rules: { required: "This field is required." },
        },
        {
            id: 'firstName',
            label: 'First Name',
            rules: { required: "This field is required." },
        },
        {
            id: 'lastName',
            label: 'Last Name',
            rules: { required: "This field is required." },
        },
        {
            id: 'birthDate',
            label: 'Birth Date',
            type: 'date',
            rules: { required: "This field is required.", },
        }
    ];

    return (
        <Form fields={fields} formProps={methods} submitCallback={onSubmit} />
    );
}
