import useDynamicOptions from "../../hooks/useDynamicOptions";

export default function FormFields() {
    const groups = useDynamicOptions(`/groups`, 'groupList', false );

    return [
        {
            id: 'givenId',
            label: 'Given ID',
            rules: {
                required: true,
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
            rules: { required: true },
        },
        {
            id: 'lastName',
            label: 'Last Name',
            rules: { required: true },
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
            rules: { required: true },
        },
        {
            id: 'groups',
            label: 'Groups',
            type: 'select',
            multiple: true,
            rules: { required: true },
            options: groups,
            bsProps: { closeMenuOnSelect: false }
        }
    ];
}