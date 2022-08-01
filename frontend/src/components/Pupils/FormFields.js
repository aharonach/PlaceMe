export default function FormFields() {
    return [
        {
            id: 'givenId',
            label: 'Given ID',
            rules: {
                required: "This field is required.",
                maxLength: {
                    value: 9,
                    message: "9 digits only."
                },
                minLength: {
                    value: 9,
                    message: "9 digits only.",
                },
                validate: v => /^\d+$/.test(v) || "Only digits."
            },
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
            id: 'gender',
            label: 'Gender',
            rules: { required: 'This field is required.' },
            type: 'radio',
            bsProps: { inline: true },
            options: [
                { value: 'MALE', label: 'Male' },
                { value: 'FEMALE', label: 'Female' }
            ]
        },
        {
            id: 'birthDate',
            label: 'Birth Date',
            type: 'date',
            rules: { required: "This field is required.", },
        }
    ];
}