import HtmlForm from "../Forms/HtmlForm";
import useFetchList from "../../hooks/useFetchList";
import {Alert} from "react-bootstrap";
import {useForm} from "react-hook-form";
import {extractListFromAPI, setFormValues} from "../../utils";
import {useEffect} from "react";
import Loading from "../Loading";

export default function AttributeValues({ pupil, group, rows }) {
    const form = useForm();
    // eslint-disable-next-line no-unused-vars
    const [values, error, loading, fetch, getValues] = useFetchList({
        fetchUrl: `/pupils/${pupil.id}/groups/${group.id}/attributes`,
        propertyName: 'attributeValueList',
        thenCallback: (res) => {
            const mapped = {};
            extractListFromAPI(res, 'attributeValueList', (attributeValue) => {
                mapped[`attribute-${attributeValue.attribute.id}`] = attributeValue.value;
            });
            setFormValues(form, mapped);
        }
    });

    useEffect(() => {
        getValues();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [pupil]);

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            <Loading show={loading} />
            <HtmlForm
                formProps={form}
                fields={prepareFields(values)}
                disabled={true}
                loading={loading}
                rows={rows}
            />
        </>
    )
}

const prepareFields = (values) => {
    const fields = [];

    values?.forEach( value => {
        fields.push({
            id: `attribute-${value.attribute.id}`,
            label: value.attribute.name,
            type: 'range',
            value: value.value,
            description: value.attribute.description,
            bsProps: { step: 1, min: 0, max: 5 },
        });
    });

    return fields;
}